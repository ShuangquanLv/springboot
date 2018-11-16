#!/bin/bash
path="./webapps/ROOT/"
if [ $# -ne 0 ]; then path=$1
fi
# ==========================清理jsp文件==========================
jspfs=0
for jspf in `find $path -type f -name '*.jsp'`
do
  # 统计jsp文件数量
  #echo $jspf
  jspfs=$(($jspfs+1))
  sed -i "s/?v=[a-z0-9]*//g" $jspf
done
# ==========================编译js文件（全局任何js文件名需唯一）==========================
jsfs=0
jsfs2=0
for jsf in `find $path -type f -name '*.js'`
do
  # 统计扫描的js文件
  jsfs=$(($jsfs+1))
  #echo $jsf
  # 获取不带路径的文件名
  jsfn=${jsf##*'/'}
  echo $jsfn
  # 对文件内容进行md5加密，获取密文串
  md5=`md5sum $jsf | cut -d' ' -f1`
  #echo $md5
  # 文件版本号
  njsf=${jsf}"?v="${md5}
  #echo $njsf
  njsfn=${jsfn}"?v="${md5}
  echo $njsfn
  for f in `find $path -name '*.jsp' | xargs grep "${jsfn}" -srl`
  do
    # 统计引用js的jsp文件
    jsfs2=$(($jsfs2+1)) 
    echo $f
    # 文件内容替换，例如 lend.js?v=328d492b48c5325eeebb6c3f4ddcecf5
    #sed -i "s/?v=[a-z0-9]*//g" $f
    sed -i "s/${jsfn}/${njsfn}/g" $f
  done
done
# ==========================编译css文件（全局任何css文件名需唯一）==========================
cssfs=0
cssfs2=0
for cssf in `find $path -type f -name '*.css'`
do
  # 统计扫描的js文件
  cssfs=$(($cssfs+1))
  # 获取不带路径的文件名
  cssfn=${cssf##*'/'}
  echo $cssfn
  # 对文件内容进行md5加密，获取密文串
  md5=`md5sum $cssf | cut -d' ' -f1`
  #echo $md5
  # 文件版本号
  ncssf=${cssf}"?v="${md5}
  #echo $njsf
  ncssfn=${cssfn}"?v="${md5}
  echo $ncssfn
  for f in `find $path -name '*.jsp' | xargs grep "${cssfn}" -srl`
  do
    # 统计引用js的jsp文件
    cssfs2=$(($cssfs2+1)) 
    echo $f
    # 文件内容替换
    #sed -i "s/?v=[a-z0-9]*//g" $f
    sed -i "s/${cssfn}/${ncssfn}/g" $f
  done
done

echo "Has ${jspfs} jsp files!"
echo "Scan ${jsfs} javascript files, and has replace ${jsfs2} jsp files."
echo "Scan ${cssfs} css files, and has replaced ${cssfs2} jsp files."