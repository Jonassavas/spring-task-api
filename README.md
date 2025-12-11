# spring-task-api

This repository contains an in progress REST API for a taskmanager like Trello. I found Trello boards to be the most effective way for me to track the assignments and deadlines during my university studies. However, I had to customize the experience quite heavily with extensions that allowed me to for instance set colors for the specific cards (task groups), and crucially to allow me to have the layout of these task groups to be stacked or customized to my liking.

This API will save your configuration with specified task group colors, layout structure, and easily create/delete and manage tasks in your task groups. These settings will be saved to a PostGreSQL database here, and utilized in a frontend application in the future.