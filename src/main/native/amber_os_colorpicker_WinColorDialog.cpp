#include "amber_os_Win.h"
#include "jni_utils.h"
#include <windows.h>
#include <commdlg.h>

#define swap(i) (GetRValue(i) << 16) | (GetGValue(i) << 8 ) | GetBValue(i)

extern "C" JNIEXPORT jboolean JNICALL Java_amber_os_colorpicker_WinColorDialog_showNative(JNIEnv *env, jobject self) {
    CHOOSECOLOR cc = {sizeof (CHOOSECOLOR), 0};

    static COLORREF custom_colors[16];
    cc.hwndOwner = (HWND) JGetLong(env, self, "hwnd");
    cc.lpCustColors = (LPDWORD) custom_colors;
    cc.rgbResult = (COLORREF) swap((JGetInt(env, self, "initial")));
    cc.Flags = CC_FULLOPEN | CC_RGBINIT;

    bool ret = ChooseColor(&cc);

    JSetLong(env, self, "error", CommDlgExtendedError());

    if (ret) {
        JSetInt(env, self, "color", swap(cc.rgbResult));
    }
    return ret;
}