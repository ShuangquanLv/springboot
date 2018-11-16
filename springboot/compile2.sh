#!/bin/bash
path="./webapps/ROOT/"
if [ $# -ne 0 ]; then path=$1
fi
# ==========================����jsp�ļ�==========================
jspfs=0
for jspf in `find $path -type f -name '*.jsp'`
do
  # ͳ��jsp�ļ�����
  #echo $jspf
  jspfs=$(($jspfs+1))
  sed -i "s/?v=[a-z0-9]*//g" $jspf
done
# ==========================����js�ļ���ȫ���κ�js�ļ�����Ψһ��==========================
jsfs=0
jsfs2=0
for jsf in `find $path -type f -name '*.js'`
do
  # ͳ��ɨ���js�ļ�
  jsfs=$(($jsfs+1))
  #echo $jsf
  # ��ȡ����·�����ļ���
  jsfn=${jsf##*'/'}
  echo $jsfn
  # ���ļ����ݽ���md5���ܣ���ȡ���Ĵ�
  md5=`md5sum $jsf | cut -d' ' -f1`
  #echo $md5
  # �ļ��汾��
  njsf=${jsf}"?v="${md5}
  #echo $njsf
  njsfn=${jsfn}"?v="${md5}
  echo $njsfn
  for f in `find $path -name '*.jsp' | xargs grep "${jsfn}" -srl`
  do
    # ͳ������js��jsp�ļ�
    jsfs2=$(($jsfs2+1)) 
    echo $f
    # �ļ������滻������ lend.js?v=328d492b48c5325eeebb6c3f4ddcecf5
    #sed -i "s/?v=[a-z0-9]*//g" $f
    sed -i "s/${jsfn}/${njsfn}/g" $f
  done
done
# ==========================����css�ļ���ȫ���κ�css�ļ�����Ψһ��==========================
cssfs=0
cssfs2=0
for cssf in `find $path -type f -name '*.css'`
do
  # ͳ��ɨ���js�ļ�
  cssfs=$(($cssfs+1))
  # ��ȡ����·�����ļ���
  cssfn=${cssf##*'/'}
  echo $cssfn
  # ���ļ����ݽ���md5���ܣ���ȡ���Ĵ�
  md5=`md5sum $cssf | cut -d' ' -f1`
  #echo $md5
  # �ļ��汾��
  ncssf=${cssf}"?v="${md5}
  #echo $njsf
  ncssfn=${cssfn}"?v="${md5}
  echo $ncssfn
  for f in `find $path -name '*.jsp' | xargs grep "${cssfn}" -srl`
  do
    # ͳ������js��jsp�ļ�
    cssfs2=$(($cssfs2+1)) 
    echo $f
    # �ļ������滻
    #sed -i "s/?v=[a-z0-9]*//g" $f
    sed -i "s/${cssfn}/${ncssfn}/g" $f
  done
done

echo "Has ${jspfs} jsp files!"
echo "Scan ${jsfs} javascript files, and has replace ${jsfs2} jsp files."
echo "Scan ${cssfs} css files, and has replaced ${cssfs2} jsp files."