# FSM-Implementation-2

## Getting Started
Welcome to the FSMtA v2 repository! If you want to get started, simply download and install GraphViz and run the software using the main method in the FSMtAUI package, in FSMtAUI.class.

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
  - **support.event package**: used for all the different possible types of Event objects that an FSM will have.
  - **support.transition package**: used for all the different possible types of transition objects that an FSM will have.
- **test package**: used for testing purposes for using the functionality without using the actual UI when writing code. Not for general use.
- **config.properties**: file with the configuration settings for GraphViz for FSMtAUI.

## Known problems
Currently, we are rejigging what types of FSMs there are to simplify it. We will have a much simpler setup after this in the fsm package. The problem is that right now, there are some functions which do not work when using the GUI (or the backend, for that matter). These pieces should come back together in the near future.

### Immediate To-Do List:
- Add file i/o for the new obscont FSMs
- Remove the redundant FSMs (already removed from the GUI)
- Add the create observer view functionality
- Add the above to the UI

## How to build an FSM from scratch
To build an FSM from the beginning...

1. Start the application.
2. Select a working directory.
3. Type a name for the FSM in the left settings pane, where the tab should read "File Options".
4. Select the FSM Type.
5. Select "Create Empty FSM".
6. Go to the "Modify FSM" tab.
7. Click "Event Options" and type the "From state", press tab, type the "To state", press tab, type the "Name", and press enter.
8. Repeat step 7 until all the events have been added.
9. Use the other options in the "Modify FSM" tab to edit the properties easily.

## Questions? Comments?
We love feedback! That's why we're putting our email right here in invisible ink. If you're reading this, you're probably our supervisor. So yeah. Email us.
