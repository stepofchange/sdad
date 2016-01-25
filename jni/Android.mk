LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_core
LOCAL_SRC_FILES := prebuild/libjniopencv_core.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_core
LOCAL_SRC_FILES := prebuild/libopencv_core.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_imgproc
LOCAL_SRC_FILES := prebuild/libopencv_imgproc.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_imgproc
LOCAL_SRC_FILES := prebuild/libjniopencv_imgproc.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_imgcodecs
LOCAL_SRC_FILES := prebuild/libjniopencv_imgcodecs.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_imgcodecs
LOCAL_SRC_FILES := prebuild/libopencv_imgcodecs.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_videoio
LOCAL_SRC_FILES := prebuild/libjniopencv_videoio.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_videoio
LOCAL_SRC_FILES := prebuild/libopencv_videoio.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_highgui
LOCAL_SRC_FILES := prebuild/libopencv_highgui.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_highgui
LOCAL_SRC_FILES := prebuild/libjniopencv_highgui.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_ml
LOCAL_SRC_FILES := prebuild/libopencv_ml.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_ml
LOCAL_SRC_FILES := prebuild/libjniopencv_ml.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_objdetect
LOCAL_SRC_FILES := prebuild/libopencv_objdetect.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_objdetect
LOCAL_SRC_FILES := prebuild/libjniopencv_objdetect.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libopencv_face
LOCAL_SRC_FILES := prebuild/libopencv_face.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)
LOCAL_MODULE := libjniopencv_face
LOCAL_SRC_FILES := prebuild/libjniopencv_face.so
include $(PREBUILT_SHARED_LIBRARY)
include $(CLEAR_VARS)

#OPENCV_LIB_TYPE:=STATIC
#OPENCV_CAMERA_MODULES:=on
#OPENCV_INSTALL_MODULES:=on
#OPENCV_LIB_TYPE:=SHARED
include ../../OpenCV-android-sdk/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES  := DetectionBasedTracker_jni.cpp
LOCAL_C_INCLUDES += $(LOCAL_PATH)
LOCAL_LDLIBS     += -llog -ldl

LOCAL_MODULE     := detection_based_tracker

include $(BUILD_SHARED_LIBRARY)


