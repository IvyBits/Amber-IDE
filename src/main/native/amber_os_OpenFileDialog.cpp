#include "amber_os_Utilities.h"
#include <windows.h>
#include <shlwapi.h>
#include <commdlg.h>

#define OFDERROR { ret = false; goto cleanup; }

extern "C" JNIEXPORT jboolean JNICALL Java_amber_os_OpenFileDialog_showNative(JNIEnv *env, jobject self) {
    OPENFILENAME ofn = { sizeof(OPENFILENAME), 0 };
    jclass cls = env->GetObjectClass(self);
    jfieldID fid;
    jstring jstr, jfilter, jinitial, jtitle;
    const jchar *jbuf;
    
    bool ret = true, multifile;
    LPWSTR szFilename = NULL;
    DWORD dwFilename;
    
    fid = env->GetFieldID(cls, "hwnd", "J");
    if (!fid)
        return false;
    ofn.hwndOwner = (HWND) env->GetLongField(self, fid);
    
    fid = env->GetFieldID(cls, "filter", "Ljava/lang/String;");
    if (!fid)
        return false;
    jfilter = (jstring) env->GetObjectField(self, fid);
    if (jfilter) {
        ofn.lpstrFilter = (LPWSTR) env->GetStringChars(jfilter, NULL);
        if (!ofn.lpstrFilter)
            return false;
    }

    fid = env->GetFieldID(cls, "initial", "Ljava/lang/String;");
    if (!fid)
        OFDERROR;
    jinitial = (jstring) env->GetObjectField(self, fid);
    if (jinitial) {
        ofn.lpstrInitialDir = (LPWSTR) env->GetStringChars(jinitial, NULL);
        if (!ofn.lpstrInitialDir)
            OFDERROR;
    }

    fid = env->GetFieldID(cls, "title", "Ljava/lang/String;");
    if (!fid)
        OFDERROR;
    jtitle = (jstring) env->GetObjectField(self, fid);
    if (jtitle) {
        ofn.lpstrTitle = (LPWSTR) env->GetStringChars(jtitle, NULL);
        if (!ofn.lpstrTitle)
            OFDERROR;
    }

    fid = env->GetFieldID(cls, "file", "Ljava/lang/String;");
    if (!fid)
        OFDERROR;
    jstr = (jstring) env->GetObjectField(self, fid);
    if (jstr) {
        jbuf = env->GetStringChars(jstr, NULL);
        if (!jbuf)
            OFDERROR;
        dwFilename = max(env->GetStringLength(jstr), 65536);
        szFilename = (LPWSTR) HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, dwFilename * sizeof(WCHAR));
        if (!szFilename)
            OFDERROR;
        memcpy(szFilename, jbuf, env->GetStringLength(jstr) * sizeof(WCHAR));
        env->ReleaseStringChars(jstr, jbuf);
    } else {
        dwFilename = 65536;
        szFilename = (LPWSTR) HeapAlloc(GetProcessHeap(), HEAP_ZERO_MEMORY, dwFilename * sizeof(WCHAR));
        if (!szFilename)
            OFDERROR;
    }
    ofn.lpstrFile = szFilename;
    ofn.nMaxFile = dwFilename;
    
    ofn.Flags = OFN_EXPLORER | OFN_NOCHANGEDIR | OFN_HIDEREADONLY;
    fid = env->GetFieldID(cls, "multi", "Z");
    if (!fid)
        return false;
    multifile = env->GetBooleanField(self, fid);
    if (multifile)
        ofn.Flags |= OFN_ALLOWMULTISELECT;
    
    ret = GetOpenFileName(&ofn);

    fid = env->GetFieldID(cls, "error", "J");
    if (fid)
        env->SetLongField(self, fid, CommDlgExtendedError());

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
            fid = env->GetFieldID(cls, "files", "[Ljava/lang/String;");
            if (!fid)
                OFDERROR;
            env->SetObjectField(self, fid, array);
            fid = env->GetFieldID(cls, "file", "Ljava/lang/String;");
            if (!fid)
                OFDERROR;
            env->SetObjectField(self, fid, first);
        } else {
            fid = env->GetFieldID(cls, "file", "Ljava/lang/String;");
            if (!fid)
                OFDERROR;
            env->SetObjectField(self, fid, env->NewString((jchar*)szFilename, lstrlen(szFilename)));
        }
    }
    
    cleanup:
    if (ofn.lpstrFilter)
        env->ReleaseStringChars(jfilter, (jchar*) ofn.lpstrFilter);
    if (ofn.lpstrInitialDir)
        env->ReleaseStringChars(jinitial, (jchar*) ofn.lpstrInitialDir);
    if (ofn.lpstrTitle)
        env->ReleaseStringChars(jtitle, (jchar*) ofn.lpstrTitle);
    if (szFilename)
        HeapFree(GetProcessHeap(), 0, szFilename);
    return ret;
}
