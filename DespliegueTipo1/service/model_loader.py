# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

# ------------------------
# Cargando modelo de disco
# ------------------------
import tensorflow as tf

def cargarModeloH5():

    MODEL_H5_FILE = "flowers_model_tl.h5"
    MODEL_H5_PATH = "../../../models/keras/"

    # Cargar el modelo DL desde disco
    loaded_model = tf.keras.models.load_model(MODEL_H5_PATH + MODEL_H5_FILE)
    print(MODEL_H5_FILE, " cargado de disco >> ", loaded_model)

    return loaded_model
