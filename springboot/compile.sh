#!/bin/bash
path="./webapps/ROOT/"
if [ $# -ne 0 ]; then path=$1
fi
#echo $path
jsfs=0
jsfs2=0
for jsf in `find $path -type f -name '*.js'`
do
  # ��$(())�����ʽ����˫�����У�����ʵ����������Ĺ���
  jsfs=$(($jsfs+1))
  #echo $jsf
  # ���һ����б������λ��
  #ind=$(expr index ${jsf} '_md5_')
  #if [ $ind -eq 0 ]; then
  #fi
  # ��ȡ����·�����ļ���
  jsfn=${jsf##*'/'}
  #echo $jsfn
  # ���ļ����ݽ���md5���ܣ���ȡ���Ĵ�
  md5=`md5sum $jsf | cut -d' ' -f1`
  #echo $md5
  # �ļ���׺�滻
  njsf=${jsf%'.js'}"_md5_"${md5}".js"
  #echo $njsf
  njsfn=${jsfn%'.js'}"_md5_"${md5}".js"
  #echo $njsfn
  if [ ! -f "$njsf" ]; then
    jsfs2=$(($jsfs2+1))
    # �ļ�����
    #cp $jsf $njsf
    # �ַ����滻
    grep "${jsfn}" -rl $path *.jsp
    #grep $jsfn -rl $path *.jsp | xargs sed -i "s/${jsfn}/${njsfn}/g"
  fi  
done
echo "Scan ${jsfs} javascript files, and has compiled ${jsfs2}."