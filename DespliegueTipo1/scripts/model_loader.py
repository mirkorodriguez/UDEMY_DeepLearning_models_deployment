# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

# ------------------------
# Cargando modelo de disco
# ------------------------
import tensorflow as tf

def cargarModelo():

    VGG_H5_FILE = "vgg19.h5"
    RESNET_H5_FILE = "resnet50.h5"
    MODEL_PATH = "../../models/keras/"

    # Cargar la RNA desde disco
    vgg_loaded_model = tf.keras.models.load_model(MODEL_PATH + VGG_H5_FILE)
    resnet_loaded_model = tf.keras.models.load_model(MODEL_PATH + RESNET_H5_FILE)
    print(VGG_H5_FILE, " cargado de disco >> ", vgg_loaded_model)
    print(RESNET_H5_FILE, " cargado de disco >> ", resnet_loaded_model)

    # graph = tf.get_default_graph()
    return vgg_loaded_model, resnet_loaded_model
