Archiving this project 10/16/23:  This project is no longer being maintained.  Forks welcome.  


# Monero PocketNode - A Monero node for your Android Device.

## Overview

THIS IS A WORK IN PROGRESS.  USE AT YOUR OWN RISK.  PR'S/FORKS WELCOME!  RECOMMEND USING ON AN OLD PHONE- NOT YOUR DAILY DRIVER.  I DON'T KNOW HOW TO WRITE ANDROID APPS.

This app is meant first and foremost for people with Android phones that have SD slots that are laying in a drawer that can be repurposed into a constantly-plugged-in, extremely low power node. I don't recommend using this on internal storage (though I am using this myself)... You're taking the internal SSD life into your own hands if you use this feature.

Important things that probably don't work:

1. Syncing with the screen off with the phone UNPLUGGED (it SHOULD continue syncing when plugged in).  You can download an app called 'Coffee' if you need to have the phone continue syncing while not plugged in.  

## Device requirements
A 64 bit processor with 2 GB of RAM and for storage, an SD slot or 128 GB of internal storage is recommended to run on the mainnet blockchain.

## Install 

Important:  After installing, be sure to turn of Android battery optimization for this app, otherwise Android will auto stop it after awhile.
It's typically found somewhere like: Settings > Apps > Pocket Node > App Battery Usage > Turn on Unrestricted (not Optimized).  

### Secure Install
Download Android Studio, replace the monero64/32 binaries with your own Android ARMv7/v8 binaries and generate a signed .apk.

OR

### Just Trust CryptoGrampy Simple Install
(If you trust that I haven't added nefarious binaries) Download the .apk from the (latest release)[https://github.com/CryptoGrampy/xmr-pocket-node/releases], and start up the app. 

## Run it

Again, first make sure battery optimization is turned off or set to unrestricted for PocketNode.

To Start the node- hit the toggle on the Main screen.  Check out the settings menu for more fine-grained control over your Node.  That's it!

You can also expose your new node as a Tor hidden service by downloading Orbot from FDroid, and going through their create hidden service feature in the top right corner.  Point the hidden service service at port 18081 (this is your node's RPC port), and you can expose whatever port you want- 80, 18081, etc.  

## Donate

If you want to buy coffee for an 85 year old: 8BudmXKZwpXhfVGCtgFPyKWgLcDLYJ5jRT95xCp4JMFWapgTLrun41AG6LPbef7WFA8T531QGnZT51cDF6uF9HECDhibEVw
