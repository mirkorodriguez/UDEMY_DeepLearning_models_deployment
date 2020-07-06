# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

environment=$1
FOLDER=$2

echo "Conda Env: $environment"
echo "Folder to Download: $FOLDER"
# borrar carpetas
rm -rf "$FOLDER/*"

echo "Iniciando descarga ..."
conda activate $environment
pip install gdown
cd $FOLDER
gdown --id 1-0zntKNE1YWZYpY6ruwUmbkYWq8Vi1D9 -O model.zip
# https://drive.google.com/file/d/1-0zntKNE1YWZYpY6ruwUmbkYWq8Vi1D9/view?usp=sharing
gunzip model.zip
conda deactivate

# Listar archivos
cd $FOLDER
ls -l $FOLDER
