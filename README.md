# Tasky

## Introduction
Tasky is a _command-line_ calendar program that aims to accommodate busy users that are capable of typing quickly,
such as students or office workers.

This is an open source project for CS2103 module.

## Adding a task

	add task123 date 11 sep 2015

This is an example to add a task using the *add* keyword with description "task123" and date 11 sep 2015. This will also store the task to a text file, which could be retrieved later by using other commands or opening the text file manually.

You can also omit the year which will then interpreted by the program as the current year, or you can omit the date entirely as well, to store the task without any date information.

## Displaying tasks

	display

This is an example to display all tasks currently stored in memory (and file). Tasky will then display a numbered list of tasks.

## Editing a task

	edit 1 task456 date 12 sep 2015

This is an example to edit the task number 1 from the [display](#displaying-tasks) to task456 and change the date to 12 sep 2015

## Removing a task

	remove 1

This is an example to remove the task that is currently number one in the list. To get the list of tasks, you can issue a display command. This command will also delete the task in the storage file. It is possible to revert the command by issuing an undo command. For more info, please take a look at [display](#displaying-tasks) and [undo](#undoing-commands)

## Undoing commands

	undo

This is an example to undo the previous command. If there is no previous command, Tasky will do nothing and give you a notification that you can not undo. All update operations done by Tasky are recorded inside the main memory of Tasky and would be wiped upon program termination. Therefore, you can only undo a command if you issued it in the same session.

Note that only add, edit and delete commands are supported.

## Changing save file path

	savepath new_file.txt

This is an example to change the path to the save file to new_file.txt. After this command, any changes made will be saved to the new file.

## Exiting the program
You can exit the program by issuing the command

	exit

