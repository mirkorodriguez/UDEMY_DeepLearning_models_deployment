# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

$environment=$1
$FOLDER=$2

echo "Conda env: $environment"
echo "Folder to download: $FOLDER"
# Activate environment
conda activate $environment
# borrar carpetas
rm -rf "$FOLDER/*"

echo "Iniciando descarga ..."
pip install gdown
gdown --id 1mXeqvGcbIlW419F1N2QD1XXvEeMlKawO -O "$FOLDER/"
# https://drive.google.com/drive/folders/1mXeqvGcbIlW419F1N2QD1XXvEeMlKawO?usp=sharing

# Deactivate current environment
conda deactivate

ls -l $FOLDER
