**The Bully Algorithm**
=========================

https://github.com/TheNilesh/bully/  
License: Public Domain, no warranty  
Nilesh Akhade

About
=====

Distributed systems starts multiple processes on different sites, all of them works in parallel.
These processes works together to perform a task. They must be coordinated for deadlock avoidance and entry into critical section of shared memory.
The coordinator process among participating processes coordinates the task.
The coordinator process is elected using election algorithms like Bully Algorithm.

Usage
=====
Create a process details file, say procList.txt
```
	This_file_contains_list_of_process
	521 proc2 2 127.0.0.1:4514
	124 proc1 1 127.0.0.1:4512
	524 proc3 3 127.0.0.1:4042
	348 proc4 4 127.0.0.1:5214
	435 proc5 5 127.0.0.1:4049
```
In above file, first column is unique PID, second is Image Name, third is site URL(site:port)

```
	java Bully procList.txt 2
```

Here procList.txt is the name of file having list of processes, and 2 is index of current process details into list of processes.

When process wants to enter a critical section in shared memory, it must request to the coordinator. Using command `CRTC` user can initiate request,
which then initiates election process if coordinator is down. 

Algorithm
============================
Adapted from Distributed Operating Systems, by Pradeep K. Sinha

Contributing
============

Please feel free to submit issues and pull requests. I appreciate bug reports.

License
=======

This software is in the *Public Domain*. That means you can do whatever you like
with it. That includes being used in proprietary products without attribution or
restrictions. There are no warranties and there may be bugs. 

Formally we are using CC0 - a Creative Commons license to place this work in the
public domain. A copy of CC0 is in the LICENSE file. 

    "CC0 is a public domain dedication from Creative Commons. A work released
    under CC0 is dedicated to the public domain to the fullest extent permitted
    by law. If that is not possible for any reason, CC0 also provides a lax,
    permissive license as a fallback. Both public domain works and the lax
    license provided by CC0 are compatible with the GNU GPL."
      - http://www.gnu.org/licenses/license-list.html#CC0