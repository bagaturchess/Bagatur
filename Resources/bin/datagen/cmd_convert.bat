@echo off
echo Listing all .plain files in the directory:
for %%f in (C:\DATA\NNUE\plain\*.plain) do (
    call echo %%f
	call target\release\bullet-utils.exe convert --input %%f --from text --output %%f.bin --threads 8
)
pause
