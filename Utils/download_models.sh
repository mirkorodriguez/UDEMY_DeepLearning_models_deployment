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
gdown --id 1-4cedRIuBML-o2ApLgqUj61QoXSAarBM -O model.zip
# https://drive.google.com/file/d/1-4cedRIuBML-o2ApLgqUj61QoXSAarBM/view?usp=sharing
conda deactivate

# Descomprimit archivos
unzip model.zip
# Borrar archivo zip
rm -rf model.zip
cd ~

echo "Completado ..."
