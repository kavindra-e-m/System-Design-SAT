@echo off
setlocal
set "MAVEN_FALLBACK=C:\Users\kavin\.m2\wrapper\dists\apache-maven-3.9.5-bin\32db9c34\apache-maven-3.9.5\bin\mvn.cmd"
where mvn >nul 2>nul
if %ERRORLEVEL% equ 0 (
    mvn %*
) else (
    if exist "%MAVEN_FALLBACK%" (
        "%MAVEN_FALLBACK%" %*
    ) else (
        echo [ERROR] Maven not found in PATH or at %MAVEN_FALLBACK%
        exit /b 1
    )
)
