package main

import (
	"fmt"
	"net"
	"os"
	"time"
)

func main() {
	serverAddress := "127.0.0.1:3108" // Address of the Scala server
	fmt.Printf("Connecting to server at %s...\n", serverAddress)

	// Connect to the server
	conn, err := net.Dial("tcp", serverAddress)
	if err != nil {
		fmt.Printf("Error connecting to server: %v\n", err)
		os.Exit(1)
	}
	defer conn.Close()
	fmt.Println("Connected to server!")

	// Send binary data
	message := []byte(`{ "data": { "app_name": "go_app" } }`)
	fmt.Printf("Sending data: %v\n", message)

	_, err = conn.Write(message)
	if err != nil {
		fmt.Printf("Error sending data: %v\n", err)
		return
	}
	fmt.Println("Data sent!")

	// Wait for a response (if the server echoes back data)
	buffer := make([]byte, 1024)
	n, err := conn.Read(buffer)
	if err != nil {
		fmt.Printf("Error reading response: %v\n", err)
		return
	}

	fmt.Printf("Received response: %v\n", buffer[:n])

	// Simulate keeping the connection alive for a short duration
	fmt.Println("Keeping connection alive for a few seconds...")
	time.Sleep(5 * time.Second)
	fmt.Println("Closing connection.")
}
