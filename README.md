# Simple Chan
A very barebone textboard made using the standard JRE libraries for everything except the logging which is being removed.

## Why
The goal of this project was to make the simplest "functional" and extensible imageboard I could possibly do without
getting lost in complex libraries.

## Features <sub><sup><sub>or lack thereof</sub></sup></sub>

### Present
&nbsp;&nbsp;● Multiple boards per instance<br>
&nbsp;&nbsp;● No storage usage (Everything stays in memory)<br>
&nbsp;&nbsp;● Basic launch arguments for the port and boards

### Missing
Here is a list of the missing features compared to more mainstream textboard solutions:<br>
&nbsp;&nbsp;● Images<br>
&nbsp;&nbsp;● Rate-limiting<br>
&nbsp;&nbsp;● Captchas<br>
&nbsp;&nbsp;● Auto thread pruning<br>
&nbsp;&nbsp;● Admin controls<br>
&nbsp;&nbsp;● Security measures (None are really implemented here)

All these features haven't been implemented in order to keep the project as simple as possible.

## Running
```java -jar simple-chan-1.1.0.jar [--board=<board_id;board_name>] [--help] [--port=<port>]```

You can choose a specific port with the `--port=<port>` argument.

You can define custom baord with the `--board=<board_id;board_name>` argument.<br>
&nbsp;&nbsp;<i>Example: `--board=b;Random` or `"--board=g;Nerds' Den"`</i>

If no arguments are given, the application will use the default values given in the help text.

## Building
In order to build the project, you can simply run the `customFatJar` gradle task and check the [/build/libs](build/libs)
folder for the compiled jar file.

## API (Will be rewritten for v2.0 as I document the code)

GET - Using ???<br>
POST - Using form fields<br>
Responses - May return some JSON if the error is related to the API and not the URL.

### GET
**URL**: /{board-id}/thread

### POST
#### Thread
**URL**: /{board-id}/thread<br>
**Handler**: [WebBoardHandler]()

##### Fields:
<blockquote>

**message**:<br>
Specifies the post's message

**author**: *Optional*<br>
Specifies the author's name

**title**: *Optional*<br>
Specifies the post's title

##### Response:

<table>
<tr><td>Field</td><td>Type</td><td>Meaning</td></tr>
<tr><td>error</td><td>String</td>
<td>Present if an error occurred and gives details about it.</td></tr>
<tr><td>threadId</td><td>Long</td>
<td>The newly made thread's ID.</td></tr>
</table>

</blockquote>



#### Post

<blockquote>

**URL**: /{board-id}/post<br>
**Handler**: [WebBoardHandler]()

##### Fields:
<blockquote>

**thread**:<br>
Specifies in which thread the post should be posted

**message**:<br>
Specifies the post's message

**author**: *Optional*<br>
Specifies the author's name

</blockquote>

##### Response:

<table>
<tr><td>Field</td><td>Type</td><td>Meaning</td></tr>
<tr><td>error</td><td>String</td>
<td>Present if an error occurred and gives details about it.</td></tr>
<tr><td>postId</td><td>Long</td>
<td>The new post's ID.</td></tr>
<tr><td>threadId</td><td>Long</td>
<td>The thread's ID in which the post was made.</td></tr>
</table>

</blockquote>

## Configuring

TODO: Classpath overloading for config files...

## License

This project, as well as all the libraries that are used, are licensed under the [Apache V2](LICENSE) license.
