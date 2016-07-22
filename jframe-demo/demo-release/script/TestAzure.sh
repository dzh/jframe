#! /bin/sh

demo-release/bin/jframe.sh stop
rm -rf /home/azure/down/*
demo-release/bin/jframe.sh start

sleep 5
ifstat -t 1 100 >> io.txt &
iostat -tm 1 100 >> iostat.txt &

#cat demo-release/log/jf-20160705.log | grep "start down date"
#cat demo-release/log/jf-20160705.log | grep "finish down date"
#demo-release/bin/jframe.sh -m jframe.plugin.demo.azure.DemoAzurePlugin upload /home/azure/dzh_test_firmware.bin 7_dzh_test_firmware.bin

