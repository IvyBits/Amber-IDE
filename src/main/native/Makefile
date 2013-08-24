CC = cl
CFLAGS = /DWIN32 /DWIN32_LEAN_AND_MEAN /DUNICODE
LD = link
LDFLAGS = /dll
OBJS = amber_os_OS.obj amber_os_filechooser_WinFileDialog.obj
LIBS = kernel32.lib user32.lib comdlg32.lib shlwapi.lib

!IF DEFINED(MINI)
CFLAGS = /GS- $(CFLAGS)
LDFLAGS = $(LDFLAGS) /nodefaultlib /entry:DllMain
OBJS = dllmain.obj $(OBJS)
!ENDIF

amberos32.dll: $(OBJS)
	$(LD) $(LDFLAGS) /out:$@ $** $(LIBS)
	copy $@ ..\resources\native\windows\$@

dllmain.obj: dllmain.c
	$(CC) $(CFLAGS) /c dllmain.c

amber_os_filechooser_WinFileDialog.cpp: amber_os_filechooser_WinFileDialog.h
amber_os_OS.cpp: amber_os_OS.h

.cpp.obj::
	$(CC) $(CFLAGS) /c $<
