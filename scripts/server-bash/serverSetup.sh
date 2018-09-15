#!/bin/bash

ssh root@94.130.104.241 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.240 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.239 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.238 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.237 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.236 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.235 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.234 "pkill -f serverUpdateAndRun"
ssh root@94.130.104.233 "pkill -f serverUpdateAndRun.sh"
ssh root@94.130.104.232 "pkill -f serverUpdateAndRun.sh"

scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.241:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.240:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.239:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.238:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.237:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.236:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.235:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.234:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.233:/qanarySetup/Applications/  &&
scp   /Volumes/Vyas-HD/office_fraunhofer/workspace/script/serverUpdateAndRun.sh  root@94.130.104.232:/qanarySetup/Applications/  &&

ssh root@94.130.104.241 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.240 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.239 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.238 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.237 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.236 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.235 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.234 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.233 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "
ssh root@94.130.104.232 "chmod +x /qanarySetup/Applications/serverUpdateAndRun.sh "

#Give you component name here (It takes 1 or 2 component only )
#ssh root@94.130.104.241 "/qanarySetup/Applications/serverUpdateAndRun.sh qa.qanary_component-DiambiguationClass-OKBQA " &
# ssh root@94.130.104.240 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-stanford qanary_component-NED-AGDISTIS " &
# ssh root@94.130.104.239 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-Babelfy qanary_component-NED-AGDISTIS " &
# ssh root@94.130.104.238 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-TextRazor qanary_component-NED-AGDISTIS " &
# ssh root@94.130.104.237 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-MeaningCloud qanary_component-NED-AGDISTIS " &
# ssh root@94.130.104.236 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-EntityClassifier2 qanary_component-NED-AGDISTIS" &
# ssh root@94.130.104.235 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-Dandelion qanary_component-NED-AGDISTIS " &
#ssh root@94.130.104.234 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-tagme qanary_component-NED-AGDISTIS " &
#ssh root@94.130.104.233 "/qanarySetup/Applications/serverUpdateAndRun.sh qanary_component-NER-Aylien qanary_component-NED-AGDISTIS " &
# #ssh root@94.130.104.232 "/qanarySetup/Applications/serverUpdateAndRun.sh"


sleep 500
pkill -f qanarySetup
