CC = cl
CFLAGS = /DWIN32 /DWIN32_LEAN_AND_MEAN /DUNICODE
LD = link
LDFLAGS = /dll
OBJS = tk_amberide_ide_os_Win.obj tk_amberide_ide_os_filechooser_WinFileDialog.obj tk_amberide_ide_os_colorpicker_WinColorDialog.obj
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

tk_amberide_ide_os_filechooser_WinFileDialog.cpp: tk_amberide_ide_os_filechooser_WinFileDialog.h
tk_amberide_ide_os_colorpicker_WinColorDialog.cpp: tk_amberide_ide_os_colorpicker_WinColorDialog.h
tk_amberide_ide_os_Win.cpp: tk_amberide_ide_os_Win.h

.cpp.obj::
	$(CC) $(CFLAGS) /c $<
