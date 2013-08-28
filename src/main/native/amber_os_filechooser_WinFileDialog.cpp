#include "amber_os_Win.h"
#include "jni_utils.h"

#include <windows.h>
#include <shlwapi.h>
#include <commdlg.h>

#define OFDERROR { ret = false; goto cleanup; }

extern "C" JNIEXPORT jboolean JNICALL Java_amber_os_filechooser_WinFileDialog_showNative(JNIEnv *env, jobject self) {
    OPENFILENAME ofn = { sizeof(OPENFILENAME), 0 };
    jclass cls = env->GetObjectClass(self);
    jfieldID fid;
    jstring jstr, jfilter, jinitial, jtitle;
    LPWSTR jbuf;
    
    bool ret = true, multifile;
    LPWSTR szFilename = NULL;
    DWORD dwFilename;
    
    ofn.hwndOwner = (HWND) JGetLong(env, self, "hwnd");
    
    jfilter = JGetString(env, self, "filter");
    if (jfilter) {
        ofn.lpstrFilter = JGetLPWSTR(env, jfilter);
        if (!ofn.lpstrFilter)
            return false;
    }

    jinitial = JGetString(env, self, "initial");
    if (jinitial) {
        ofn.lpstrInitialDir = JGetLPWSTR(env, jinitial);
        if (!ofn.lpstrInitialDir)
            OFDERROR;
    }

    jtitle = JGetString(env, self, "title");
    if (jtitle) {
        ofn.lpstrTitle = JGetLPWSTR(env, jtitle);
        if (!ofn.lpstrTitle)
            OFDERROR;
    }

    jstr = JGetString(env, self, "file");
    if (jstr) {
        jbuf = JGetLPWSTR(env, jstr);
        if (!jbuf)
            OFDERROR;
        dwFilename = max(env->GetStringLength(jstr), 65536);
        szFilename = (LPWSTR) HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, dwFilename * sizeof(WCHAR));
        if (!szFilename)
            OFDERROR;
        memcpy(szFilename, jbuf, env->GetStringLength(jstr) * sizeof(WCHAR));
        JFreeLPWSTR(env, jstr, jbuf);
    } else {
        dwFilename = 65536;
        szFilename = (LPWSTR) HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, dwFilename * sizeof(WCHAR));
        if (!szFilename)
            OFDERROR;
    }
    ofn.lpstrFile = szFilename;
    ofn.nMaxFile = dwFilename;
    
    ofn.Flags = OFN_EXPLORER | OFN_NOCHANGEDIR | OFN_HIDEREADONLY;
    multifile = JGetBool(env, self, "multi");
    if (multifile)
        ofn.Flags |= OFN_ALLOWMULTISELECT;
    
    ret = GetOpenFileName(&ofn);

    JSetLong(env, self, "error", CommDlgExtendedError());

    if (ret) {
        if (multifile) {
            LPWSTR directory = szFilename;
            WCHAR buffer[MAX_PATH];
            DWORD dwCount = 0;
            jobjectArray array;
            jobject first;
            LPWSTR pszz;
            int i;
            
            for (pszz = szFilename + ofn.nFileOffset; *pszz; pszz += lstrlen(pszz) + 1)
                ++dwCount;
            
            array = env->NewObjectArray(dwCount, env->FindClass("java/lang/String"), NULL);
            
            for (i = 0, pszz = szFilename + ofn.nFileOffset; *pszz; pszz += lstrlen(pszz) + 1, ++i) {
                PathCombine(buffer, directory, pszz);
                env->SetObjectArrayElement(array, i, env->NewString((jchar*)buffer, lstrlen(buffer)));
            }
            
            first = env->GetObjectArrayElement(array, 0);
            JSetObject(env, self, "files", "[Ljava/lang/String;", array);
            JSetString(env, self, "file", first);
        } else {
            fid = env->GetFieldID(cls, "file", "Ljava/lang/String;");
            if (!fid)
                OFDERROR;
            JSetString(env, self, "file", JNewLPWSTR(env, szFilename));
        }
    }
    
    cleanup:
    if (ofn.lpstrFilter)
        JFreeLPWSTR(env, jfilter, ofn.lpstrFilter);
    if (ofn.lpstrInitialDir)
        JFreeLPWSTR(env, jinitial, ofn.lpstrInitialDir);
    if (ofn.lpstrTitle)
        JFreeLPWSTR(env, jtitle, ofn.lpstrTitle);
    if (szFilename)
        HeapFree(GetProcessHeap(), 0, szFilename);
    return ret;
}
