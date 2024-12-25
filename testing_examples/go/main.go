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

	// Send a single message
	if sendMessages(conn, 1) != nil {
		fmt.Println("Error sending messages.")
		return
	}
	// Send binary data
	// message := []byte(`{ "data": { "app_name": "go_app" } }`)
	// fmt.Printf("Sending data: %v\n", message)

	// _, err = conn.Write(message)
	// if err != nil {
	// 	fmt.Printf("Error sending data: %v\n", err)
	// 	return
	// }
	// fmt.Println("Data sent!")

	// // Wait for a response (if the server echoes back data)
	// buffer := make([]byte, 1024)
	// n, err := conn.Read(buffer)
	// if err != nil {
	// 	fmt.Printf("Error reading response: %v\n", err)
	// 	return
	// }

	// fmt.Printf("Received response: %v\n", buffer[:n])

	// // Simulate keeping the connection alive for a short duration
	// fmt.Println("Keeping connection alive for a few seconds...")
	// time.Sleep(1 * time.Second)
	// fmt.Println("Closing connection.")
}

func sendMessages(conn net.Conn, messageCount int) error {
	for i := 0; i < messageCount; i++ {
		sendingTime := time.Now().String()
		message := []byte(fmt.Sprintf(`{ "data": {
			"type": "info",
			"sent_at": "%s",
			"service": "go_app",
			"payload": "Hello from Go!",
			"p_type": "string"
		} }`, sendingTime))
		fmt.Printf("Sending message %d\n", i)

		_, err := conn.Write(message)
		if err != nil {
			fmt.Printf("Error sending message %d: %v\n", i, err)
			return err
		}
		fmt.Printf("Message %d sent!\n", i)

		time.Sleep(1 * time.Second / 100)
	}

	return nil
}
