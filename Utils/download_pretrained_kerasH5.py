# Developed by Mirko J. Rodríguez mirko.rodriguezm@gmail.com

# Download and save pretrained models as keras models *.h5
import tensorflow as tf

path_to_save = "models/keras/"

#Load and save the VGG model as *.h5 file
vgg_model = tf.keras.applications.VGG19(weights='imagenet')
vgg_model.save(''.join([path_to_save,'vgg19.h5']))


#Load and save the ResNet50 model as *.h5 file
resnet_model = tf.keras.applications.ResNet50(weights='imagenet')
resnet_model.save(''.join([path_to_save,'resnet50.h5']))
