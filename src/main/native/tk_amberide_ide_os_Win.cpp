#include "tk_amberide_ide_os_Win.h"
#include <windows.h>

extern "C" JNIEXPORT jstring JNICALL Java_tk_amberide_ide_os_Win_nativeGetFont(JNIEnv *env, jclass self) {
    NONCLIENTMETRICS ncmMetrics = { sizeof(NONCLIENTMETRICS) };
    LPWSTR szFontName;

    SystemParametersInfo(SPI_GETNONCLIENTMETRICS, 0, &ncmMetrics, 0);
    szFontName = ncmMetrics.lfMessageFont.lfFaceName;
    return env->NewString((jchar*)szFontName, lstrlen(szFontName));
}
