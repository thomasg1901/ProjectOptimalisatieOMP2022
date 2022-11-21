import os
from subprocess import Popen, PIPE

inputFolder = r'OMP/src/resources'
solutionFolder = r'OMP/output'

script = r'OMP/validator.py'
print(os.path)
inputFiles = os.listdir(inputFolder)
solutionFiles = os.listdir(solutionFolder)

for file in inputFiles:
    if solutionFiles.__contains__('sol-'+file):
        cmd = f'python3 {script} -i {inputFolder}/{file} -s {solutionFolder}/sol-{file}'
        p = Popen(cmd, shell=True, stdout=PIPE, stderr=PIPE, close_fds=True)
        (output, err) = p.communicate()
        output = output.decode("utf-8")

        print(file)
        print(output)
        print(err)


