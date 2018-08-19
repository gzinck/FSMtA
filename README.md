# FSM-Implementation-2

## Getting Started
Welcome to the FSMtA v2 repository! If you want to get started, first install GraphViz from graphviz.org and ensure you have an up-to-date version of Java. Then you can download the executable .jar file named FSMtA.jar; then you can run it as a normal application.

 - Important: You must install GraphViz to make this program work, it handles the image construction and is vital. (graphviz.org)

## Package Overview
FSMtAUI is split into a number of packages to organize based on function:

- **fsm package**: used for all the FSM objects and their interfaces. All the different flavours of the FSM objects are here.
  - **fsm.attribute package**: used for all the FSM interfaces.
- **fsmtaui package**: used to provide the user interface for the FSMtAUI application.
  - **fsmtaui.popups package**: used for any popups that appear in the application, such as dialog boxes and error messages.
  - **fsmtaui.settingspane package**: used for the settings pane which is displayed at the let side of the UI at all times. The settings pane has many component panes for all the options available to the user.
    - **fsmtaui.settingspane.modifyfsmpane package**: used for all the components of the settings pane with all the options for modifying the current FSM in the GUI.
    - **fsmtaui.settingspane.operationspane package**: used for all the operations that can be performed on a given FSM. It is also composed of many panes with various options for performing tasks on single or multiple FSMs.
- **graphviz package**: used for all the graphviz rendering of the FSMs.
- **support package**: used for various smaller components of the FSMs which are essential, small pieces that allow for some critical abstractions in the FSM classes.
  - **support.attribute package**: used for all the interfaces for the abstractions that the support class provides (such as those for the event objects).
  - **support.map package**: used for the various aggregate structures of States/Events/Transitions associated to an FSM.
  - **support.transition package**: used for all the different possible types of transition objects that an FSM will have.
- **test package**: used for testing purposes for using the functionality without using the actual UI when writing code. Not for general use.
- **assets**: used for storing images and vital file information packaged into the executable .jar file.

### Immediate To-Do List:
 - Receive feedback and fix any glitches that arise.

## How to use this Software (Creating a random FSM and performing operations on/with it)

1. Start the application.
2. Type a name for the FSM in the labeled text-box.
3. Select the FSM Type.
4. Select "Create Empty FSM" and assign parameters for creation.
5. Under File Options you can save the FSM in several formats.
6. Under Modify FSM you can add/remove Transitions or States, and adjust their properties.
7. Under FSM Operations you can perform functions on the FSM, standalone or with another FSM.

- .fsm or .mdl files written from this program, or yourself by following formatting, can also be read into the program.

## Troubleshooting

- Does nothing happen when you run the .jar file? This can be for one of two reasons we have experienced.
  1. Check your install of Java to ensure it is up to date. (Unlikely to be a problem, but you never know.)
  2. More likely (for Windows) is that your computer has forgotten what program to run .jar files with. Use the software here: https://johann.loefflmann.net/en/software/jarfix/index.html to reassign the correct program.
  
- Do images not load when you generate/load in an FSM? (Or the FSMs themselves don't appear as selectable objects?)
  - You may not have downloaded GraphViz properly (or at all). 
  - While not experienced, the location that you run the .jar file from may be a restricted area that does not permit the software to write files there; try moving it to a different location. (The software uses relative file paths for temporary file storage.)

## Questions? Comments?
We will set up an alias to contact us from soon.
