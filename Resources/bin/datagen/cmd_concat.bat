@echo off
setlocal enabledelayedexpansion

set "output=C:\DATA\NNUE\plain\dataset.bin"
set "directory=C:\DATA\NNUE\plain\"
set "tempfile=C:\DATA\NNUE\plain\tempfile.bin"

echo Concatenating all .bin files in the %directory% directory into %output%

:: Clear the output and temporary files if they already exist
if exist %output% del %output%
if exist %tempfile% del %tempfile%

:: Process files in chunks
set "chunkSize=33"
set /a "fileCount=0"
set "filelist="

for %%f in (%directory%\*.bin) do (
    set /a "fileCount+=1"
    if "!filelist!" == "" (
        set "filelist=%%f"
    ) else (
        set "filelist=!filelist! + %%f"
    )
    
    if !fileCount! geq %chunkSize% (
        :: Echo the file list for the current chunk
        echo Processing chunk: !filelist!
        
        :: Concatenate the current chunk of files
        copy /b !filelist! %tempfile%
        
        :: Reset for the next chunk
        set "fileCount=0"
        set "filelist=%tempfile%"
    )
)

:: Process any remaining files
if defined filelist (
    echo Processing remaining files: !filelist!
    copy /b !filelist! %tempfile% >nul
)

:: Move the temporary file to the final output file
move %tempfile% %output%

echo Concatenation complete. Output file is %output%
pause
