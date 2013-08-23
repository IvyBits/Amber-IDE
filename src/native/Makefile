JDK = C:\Program Files\Java\JDK
CC = cl
CFLAGS = /DWIN32 /DWIN32_LEAN_AND_MEAN /DUNICODE "/I$(JDK)\include" "/I$(JDK)\include\win32"
LD = link
LDFLAGS = /dll
OBJS = amber_os_OpenFileDialog.obj amber_os_Utilities.obj
LIBS = kernel32.lib user32.lib comdlg32.lib shlwapi.lib

!IF DEFINED(MINI)
CFLAGS = /GS- $(CFLAGS)
LDFLAGS = $(LDFLAGS) /nodefaultlib /entry:DllMain
OBJS = dllmain.obj $(OBJS)
!ENDIF

amberos32.dll: $(OBJS)
	$(LD) $(LDFLAGS) /out:$@ $** $(LIBS)
	copy $@ ..\main\resources\native\windows\$@

dllmain.obj: dllmain.c
	$(CC) $(CFLAGS) /c dllmain.c

amber_os_OpenFileDialog.cpp: amber_os_OpenFileDialog.h
amber_os_Utilities.cpp: amber_os_Utilities.h

.cpp.obj::
	$(CC) $(CFLAGS) /c $<
