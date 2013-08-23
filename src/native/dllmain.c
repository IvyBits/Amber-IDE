#include <windows.h>

BOOL WINAPI DllMain(HINSTANCE hinstDLL, DWORD fdwReason, LPVOID lpvReserved) {
    return TRUE;
}

void *memset(void *s, int c, size_t n) {
    char* p = (char*) s;
    while (n--)
        *p++ = (char) c;
    return s;
}

void *memcpy(void *dest, const void *src, size_t n) {
    char *dp = (char*) dest;
    const char *sp = (const char*)src;
    while (n--)
        *dp++ = *sp++;
    return dest;
}
