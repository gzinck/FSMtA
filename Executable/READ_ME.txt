To use this, run the .bat program (to include the required Java modules in running the .jar program).
You will need to change the directory leading to the .../bin/java to wherever your Java sdk is stored.
Likely easiest to make a new .bat file (if on Windows) and include the following (in the same directory as FSMtA.jar):
"[path_to_java]/bin/java --module-path "./FSMtA_lib/lib" --add-modules javafx.controls,javafx.fxml -jar FSMtA.jar"

If you want to move this out of the Executable folder in the Github project, make sure to bring along the FSMtA_lib folder,
the FSMtA.jar, and the matching .bat file to run it. (The program will generate files in the same directory these files
are stored in.)