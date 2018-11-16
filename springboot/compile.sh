#!/bin/bash
path="./webapps/ROOT/"
if [ $# -ne 0 ]; then path=$1
fi
#echo $path
jsfs=0
jsfs2=0
for jsf in `find $path -type f -name '*.js'`
do
  # 用$(())将表达式放在双括号中，即可实现四则运算的功能
  jsfs=$(($jsfs+1))
  #echo $jsf
  # 最后一个反斜杠索引位置
  #ind=$(expr index ${jsf} '_md5_')
  #if [ $ind -eq 0 ]; then
  #fi
  # 获取不带路径的文件名
  jsfn=${jsf##*'/'}
  #echo $jsfn
  # 对文件内容进行md5加密，获取密文串
  md5=`md5sum $jsf | cut -d' ' -f1`
  #echo $md5
  # 文件后缀替换
  njsf=${jsf%'.js'}"_md5_"${md5}".js"
  #echo $njsf
  njsfn=${jsfn%'.js'}"_md5_"${md5}".js"
  #echo $njsfn
  if [ ! -f "$njsf" ]; then
    jsfs2=$(($jsfs2+1))
    # 文件复制
    #cp $jsf $njsf
    # 字符串替换
    grep "${jsfn}" -rl $path *.jsp
    #grep $jsfn -rl $path *.jsp | xargs sed -i "s/${jsfn}/${njsfn}/g"
  fi  
done
echo "Scan ${jsfs} javascript files, and has compiled ${jsfs2}."