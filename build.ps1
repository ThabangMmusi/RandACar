# Ensure output directory exists
if (-not (Test-Path -Path "out")) {
    New-Item -ItemType Directory -Path "out"
}

# Define classpath
$jarPath = ".\lib\miglayout-4.0.jar;.\lib\TimingFramework-0.55.jar"

# Compile Java files
javac -cp $jarPath -d out (Get-ChildItem -Recurse -Filter *.java).FullName

# Run main class from correct package
java -cp ".\out;$jarPath" com.main.RentalEstimator
