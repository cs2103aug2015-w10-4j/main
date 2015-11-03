# Tasky

## Introduction
Tasky is a _command-line_ calendar program that aims to accommodate busy users that are capable of typing quickly,
such as students or office workers.

This is an open source project for CS2103 module.

## Installation
To install Tasky, clone the repository using the following command:
	
	git clone git://github.com/cs2103aug2015-w10-4j/main.git
	
Then, enter the downloaded "main" folder, and type the following to compile the source code:
	
	javac -d bin -sourcepath src -cp gson-2.3.1.jar src/logic/Logic.java
	
Note that you only have to do this once for each Tasky update.
	
After compiling, execute the following command to launch Tasky!

	java -cp bin logic.Logic

## Adding a task (command: add)

	add task123 by 11 sep 2015

This is an example to add a task using the *add* keyword with description "task123" and date 11 sep 2015. It is optional to specify the year of the task, i.e. the year will be defaulted to the current year if not indicated by the user (as shown below). 

Note that the keyword to specify the end date field here is 'by'. We can also use the keyword pairs 'start... end' or 'from... to' to specify the starting and ending date-time for the event:

	add task 123 start 11 sep end 15 sep

Use of natural language date filters are also accepted:

	add task 123 by next monday
	add task 456 by tomorrow


To specify a timing for the task, simply add a time arugment after the date arguments, for e.g.

	add task 123 by today 8PM
	add task 123 start 11 sep 9AM end 11 sep 2PM

This will also store the task to a text file, which could be retrieved later by using other commands or opening the text file manually.

You can also omit the year which will then interpreted by the program as the current year, or you can omit the date entirely as well, to store the task without any date information.

To spcficy a location for the task, simply add "loc" or "at" followed by the location, for e.g.

	add task 123 by today 8PM loc nus
	add task 123 start 11 sep 9AM end 11 sep 2PM loc my home
	
To add  periodic tasks, use every [index] day(s)/week(s)/month(s)/year(s) for [index]. for e.g
       
       add task from today to tomorrow every 2 days for 2
       add task from today to tomorrow every 1 month for 5
       
noted that a starting time and a ending time is required when adding periodic task.   
	
Noted that we allowed adding task by eiher entering the location, time or periodic field first.
 
## Displaying tasks (command: display/clear)

	display

This is an example to display all tasks currently stored in memory (and file). Tasky will then display a numbered list of tasks.

## Editing a task (command: edit/change)

	edit 1 task456 by 12 sep 2015

This is an example to edit the task number 1 from the [display](#displaying-tasks) to task456 and change the date to 12 sep 2015


	change 1 loc school
	
This is an example to edit the task number 1 from the [display](#displaying-tasks) to task456 and change the location to school	

## Deleting a task (command: delete/del)

	delete 1  

This is an example to remove the task that is currently number one in the list. 

	del 1 2 4 6
	
This is an example to remove the task that is currently number one, two, four, six in the list. 


	del 1-6
	
This is an example to remove the task that is currently from number one to number six in the list.

To get the list of tasks, you can issue a display command. This command will also delete the task in the storage file. It is possible to revert the command by issuing an undo command. For more info, please take a look at [display](#displaying-tasks) and [undo](#undoing-commands)


## Undoing commands (command: undo)

	undo

This is an example to undo the previous command. If there is no previous command, Tasky will do nothing and give you a notification that you can not undo. All update operations done by Tasky are recorded inside the main memory of Tasky and would be wiped upon program termination. Therefore, you can only undo a command if you issued it in the same session.

Note that only add, edit and delete commands are supported.

## Changing save file path

	savepath new_file.txt

This is an example to change the path to the save file to new_file.txt. After this command, any changes made will be saved to the new file.

## mark task as completed  (command: mark)

       mark 1
       
This is an example to mark task 1 that is showing on the screen completed, to view that tasks that marked completed, key in " search done".       

## unmark task as completed  (command: unmark)

       unmark 1
       
This is an example to mark task 1 that is showing on the screen completed, to view that tasks that marked completed, key in " search done".       

## search tasks (command: search/find)

       search task
       
This is an example to search any tasks that contain "task" in their name.

noted we allowed multiple level of searching. for e.g. after the first search

1. task1 loc nus
2. task2 loc home
3. task3 loc school

is displaying on the screen

key in "search loc home"

1.task2 loc home 

will be shown on the screen

we also allowed user to search according to time. for e.g.

        search by today
        
tasks that due by today will be shown on the screen


## Exiting the program
You can exit the program by issuing the command

	exit
	
	

