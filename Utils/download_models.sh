# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

# Activate DEV environment
conda activate DEV
#Main models folder
FOLDER="~/models"
# borrar carpetas
rm -rf "$FOLDER/*"

echo "Archivo $FOLDER no existe --> Descargando ..."
gdown --id 1mXeqvGcbIlW419F1N2QD1XXvEeMlKawO -O "$FOLDER/"
# https://drive.google.com/drive/folders/1mXeqvGcbIlW419F1N2QD1XXvEeMlKawO?usp=sharing

# Deactivate current environment
conda deactivate

tree $FOLDER
