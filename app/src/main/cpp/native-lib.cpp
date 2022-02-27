#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */
        ) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_calculateInteger(
        JNIEnv *env,
        jobject /*thiz*/,
        jstring first,
        jstring second, jchar op
        ) {
    const unsigned long long f = atoll(env->GetStringUTFChars(first, 0));
    const unsigned long long s = atoll(env->GetStringUTFChars(second, 0));
    unsigned long long result = 0;

    switch(op) {
        case '+': result = f+s; break;
        case '-': result = f-s; break;
        case '*': result = f*s; break;
    }

    return env->NewStringUTF(std::to_string(result).c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_myapplication_MainActivity_fact(JNIEnv *env, jobject /*thiz*/, jlong value) {
    unsigned long long temp = 1;

    /**
     * Non serve che la Cpu sprechi cicli di clock per forza con dei valori già noti o facili da ottenere
     * Riporto solo i primi 10
     */
    switch(value) {
        case 2: temp = 2; break;
        case 3: temp = 6; break;
        case 4: temp = 24; break;
        case 5: temp = 120; break;
        case 6: temp = 720; break;
        case 7: temp = 5040; break;
        case 8: temp = 40320; break;
        case 9: temp = 362880; break;
        case 10: temp = 3628800; break;
        default: {
            /**
             * In questo caso il valore immesso è certamente maggiore di 10,
             * quindi parto da questo valore
             */
            temp = 3628800;
            for (unsigned long long index = 11; index <= value; index++) {
                temp *= index;
            }
            break;
        }
    }

    return env->NewStringUTF(std::to_string(temp).c_str());
}