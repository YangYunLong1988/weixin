#!/bin/bash
# Strict mode

# Create the hash to pass to the IPython notebook, but don't export it so it doesn't appear
# as an environment variable within IPython kernels themselves

echo "========================================================================"
echo "You can now connect to this Ipython Notebook server using, for example:"
echo ""
echo "========================================================================"

java -Dacl=80LOuddoKkNuXfFpQQZOXojWLRuBDj9o -jar /app/app.jar
