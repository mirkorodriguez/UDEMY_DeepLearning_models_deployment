# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

# --------------------------------------------
# Exponiendo el servicio Web en el puerto 5000
# --------------------------------------------

#Import Flask
from flask import Flask, request, jsonify
from flask_cors import CORS

#Import Tensorflow
import tensorflow as tf

#Import libraries
import numpy as np
import os
from werkzeug.utils import secure_filename
from model_loader import cargarModelo

UPLOAD_FOLDER = '../images/uploads'
ALLOWED_EXTENSIONS = set(['png', 'jpg', 'jpeg'])

port = int(os.getenv('PORT', 5000))
print ("Port recognized: ", port)

#Initialize the application service
app = Flask(__name__)
CORS(app)
global vgg_loaded_model, resnet_loaded_model
vgg_loaded_model, resnet_loaded_model = cargarModelo()
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

#Define a route
@app.route('/')
def main_page():
	return '¡Servicio REST activo!'

# VGG
@app.route('/vgg/predict/',methods=['POST'])
def vgg():
    model_name = "vgg"
    return (predict(model_name))

# ResNet
@app.route('/resnet/predict/',methods=['POST'])
def resnet():
    model_name = "resnet"
    return (predict(model_name))



# Funciones
def allowed_file(filename):
    return '.' in filename and filename.rsplit('.', 1)[1].lower() in ALLOWED_EXTENSIONS

def predict(model_name):
    data = {"success": False}
    if request.method == "POST":
        # check if the post request has the file part
        if 'file' not in request.files:
            print('No file part')
        file = request.files['file']
        # if user does not select file, browser also submit a empty part without filename
        if file.filename == '':
            print('No selected file')
        if file and allowed_file(file.filename):
            filename = secure_filename(file.filename)
            file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))

            #loading image
            filename = UPLOAD_FOLDER + '/' + filename
            print("\nfilename:",filename)

            image_to_predict = tf.keras.preprocessing.image.load_img(filename, target_size=(224, 224))
            test_image = tf.keras.preprocessing.image.img_to_array(image_to_predict)
            test_image = np.expand_dims(test_image, axis = 0) # no es necesario normalizar /255.


            if (model_name == 'vgg'):
                image = tf.keras.applications.vgg19.preprocess_input(test_image.copy())
                predictions = vgg_loaded_model.predict(image)
                # print(predictions)
                labels = tf.keras.applications.vgg19.decode_predictions(predictions,top=5)

            if (model_name == 'resnet'):
                image = tf.keras.applications.resnet50.preprocess_input(test_image.copy())
                predictions = resnet_loaded_model.predict(test_image)
                # print(predictions)
                labels = tf.keras.applications.resnet50.decode_predictions(predictions,top=5)

            print(labels)

            ClassPred = labels[0][0][1]
            ClassProb = labels[0][0][2]

            print("Pedicción:", ClassPred)
            print("Prob: {:.2%}".format(ClassProb))

            #Results as Json
            data["predictions"] = []
            r = {"label": ClassPred, "score": float(ClassProb)}
            data["predictions"].append(r)

            #Success
            data["success"] = True

    return jsonify(data)

# Run de application
app.run(host='0.0.0.0',port=port, threaded=False)
