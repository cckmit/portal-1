#!/bin/sh
export dir_web="./web"
export dir_root="./../.."
export dir_scripts="${dir_web}/scripts"
export dir_packages="${dir_web}/packages"
export packages_regex="web/packages/.*/src/.*"
export eslint_files_regex="\.\(js\|ts\|jsx\|tsx\)$"
export prettier_files_regex="\.\(js\|ts\|jsx\|tsx\|html\|css\|scss\|sass\|json\)$"
export jest_files_regex="\.test\.\(js\|ts\|jsx\|tsx\)$"
