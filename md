#!/usr/bin/env bash
  java -version

	# Run script to initalize DB
  if [ "$1" == --initdb ]
  then
      java -cp lib/h2/bin/h2-1.4.200.jar org.h2.tools.RunScript -url jdbc:h2:~/moleculedb -user sa -script h2.sql*
      echo "Initialized DB"

  elif [ "$1" == --insertPubchem ] #checks if the options parameters exist
  then
      echo "Search... molecule from $2"
      java -cp lib/h2/bin/h2-1.4.200.jar org.h2.tools.RunScript -url jdbc:h2:~/moleculedb -user sa -script h2dump-PubChem.sql*

  elif [ "$1" == --insertTenMillion ] #checks if the options parameters exist
    then
        echo "Search... molecule from $2"
        java -cp lib/h2/bin/h2-1.4.200.jar org.h2.tools.RunScript -url jdbc:h2:~/moleculedb -user sa -script h2dump-ten-million.sql*

  else
    if [ ! -f "$2" ] #checks to see if filename parameter is in exist
    then
      echo "File $2 does not exist"

    # Needed to break these up because || statement wasnt working
    elif [ "$1" == --addMolecule ] #checks if the options parameters exist
    then
        echo "Add... molecule from $2"
        java -jar group9.jar $1 $2

    elif [ "$1" == --findMolecule ] #checks if the options parameters exist
    then
        echo "Search... molecule from $2"
        java -jar group9.jar $1 $2

    else
        echo "Operation $1 is not supported at this time."
    fi
  fi

