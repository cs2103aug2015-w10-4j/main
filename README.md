# Tasky

## Introduction
Tasky is a _command-line_ calendar program that aims to accommodate busy users that are capable of typing quickly,
such as students or office workers.

This is an open source project for CS2103 module.

## Adding a task

	add task123

This is an example to add a task using the "add" keyword. This will also store the task to a text file, which could be retrieved later by using other commands or opening the text file manually.

## Removing a task

	remove 1

This is an example to remove the task that is currently number one in the list. To get the list of tasks, you can issue a display command. This command will also delete the task in the storage file. It is possible to revert the command by issuing an undo command. For more info, please take a look at [display](#displaying-tasks) and [undo](#undoing-tasks)

## Displaying tasks

	display

This is an example to display all tasks currently stored in memory (and file). Tasky will then display numbered list of tasks.

## Undoing tasks

	undo

This is an example to undo the previous command. If there is no previous command, Tasky will do nothing and give you a notification that you can not undo. All update operations done by Tasky are recorded inside the main memory of Tasky and would be wiped upon program termination. Therefore, you can only undo a command if you issued it in the same session.
