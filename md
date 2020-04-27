#!/usr/bin/env bash
  java -version
  echo "----------------------------------------"
  echo "GROUP 9 MOLECULE COMMAND LINE INTERFACE:"
  echo "Type ./md help for commands"
  echo "----------------------------------------"

  if [ "$1" == help ]
  then
      echo "commands"
      echo "--addMolecule 'filepath'        to add a molecule to the database (filename without '') "
      echo "--findMolecule 'filepath'       to find molecules that are isomorphic to the input (filename without '') "
      echo "--initdb                        to create the database tables with some molecules. This needs to be run beforehand."
      echo "--insertPubchem                 to insert 1000 known chemicals as well as 9,000 randomly created molecules. Total of 10,000 molecules."
      echo "--insertTenMillion              to insert 10 Million randomized molecules."
  fi
    # Run script to initalize DB
  if [ "$1" == --initdb ]
  then
      java -cp lib/h2/bin/h2-1.4.200.jar org.h2.tools.RunScript -url jdbc:h2:~/moleculedb -user sa -script h2.sql*
      echo "Initialized DB"
      echo "Successfully initialized"
  fi

  if [ "$1" == --insertPubchem ] #checks if the options parameters exist
  then
      echo "Creating database of 10,000 molecules. 1000 of which are from PubChem."
      java -cp lib/h2/bin/h2-1.4.200.jar org.h2.tools.RunScript -url jdbc:h2:~/moleculedb -user sa -script h2dump-PubChem.sql*
      echo "Successfully created database with Pubchem molecules"
  fi

  if [ "$1" == --insertTenMillion ] #checks if the options parameters exist
  then
      echo "Creating database of ten million random molecules."
      java -cp lib/h2/bin/h2-1.4.200.jar org.h2.tools.RunScript -url jdbc:h2:~/moleculedb -user sa -script h2dump-ten-million.sql*
      echo "Successfully created database of ten million molecules"
  fi

  if [ "$1" == --addMolecule ] #checks if the options parameters exist
    then
    if [ ! -f "$2" ]
      then
        echo "File $2 does not exist/could not be found. Please check if the filepath is correct"
    else
        echo "Add... molecule from $2"
        java -jar group9.jar "$1" "$2"
        echo "Successful: Molecule from $2 has been added"
    fi
  fi
  # Needed to break these up because || statement wasnt working

  if [ "$1" == --findMolecule ] #checks if the options parameters exist
    then
    if [ ! -f "$2" ]
      then
        echo "File $2 does not exist/could not be found. Please check if the filepath is correct"
    else
        echo "Search... molecule from $2"
        java -jar group9.jar "$1" "$2"
        echo "Successful: Molecule from $2 has been searched"

    fi
  fi

