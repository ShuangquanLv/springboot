#!/bin/bash

# ==========================引号、反斜杠==========================
# 引号(单引号和双引号)：当shell碰到第一个单引号时，它忽略掉其后直到右引号的所有特殊字符。双引号作用与单引号类似，区别在于它没有那么严格。单引号告诉shell忽略所有特殊字符，而双引号只要求忽略大多数，具体说，括在双引号中的三种特殊字符不被忽略：$,\,` ,即双引号会解释字符串的特别意思,而单引号直接使用字符串.如果使用双引号将字符串赋给变量并反馈它，实际上与直接反馈变量并无差别。如果要查询包含空格的字符串，经常会用到双引号。
num=10
val="abc"
val2=abc
if [ $val = $val2 ]; then
  echo "${val} equals to ${val2}"
else
  echo "${val} not equals to ${val2}" 
fi
echo '$num'
echo "$num"
echo '${val}'
echo "${val}"
# 反引号(Tab键上，数字1键左)：命令替换是指shell能够将一个命令的标准输出插在一个命令行中任何位置。shell中有两种方法作命令替换：把shell命令用反引号或者$(...)结构括起来，其中，$(...)格式受到POSIX标准支持，也利于嵌套。
echo "Current time is `date`" 
num=$(($num+1))
echo $num
# 反斜杠(\)：反斜杠一般用作转义字符,或称逃脱字符,linux如果echo要让转义字符发生作用,就要使用-e选项,且转义字符要使用双引号。反斜杠的另一种作用,就是当反斜杠用于一行的最后一个字符时，shell把行尾的反斜杠作为续行，这种结构在分几行输入长命令时经常使用。
echo "\n"
echo -e "\n"

# ==========================分割字符串操作==========================
path="./webapps/ROOT/pages/common/inc_portal.jsp"
separtor="/"
# 删除从0到separtor子字符串第一次出现的位置
str=${path#*${separtor}}
echo $str
# 删除从最后一次substr开始到字符串结束的位置
str=${path##*${separtor}}
echo $str
# 删除字符串最后一次出现到字符串结束的位置的部分
str=${path%${separtor}*}
echo $str
# 删除separtor子字符串第一次出现到结束的部分
str=${path%%${separtor}*}
echo $str

# ==========================sed正则匹配替换==========================
# -i 修改源文件
sed -i "s/s/S/g" ./test.txt
# -e 多处替换，并生成一个新文件，源文件不变
sed -e "s/a/A/g" -e "s/s/S/g" ./test.txt>./test2.txt
# 支持正则匹配查找，和grep一样都需要转义
sed -i "s/cs\{2\}/css2/g" ./webapps/ROOT/pages/common/inc_portal.jsp
sed -i "s/?v=[a-z0-9A-Z]\{10,\}//g" ./test.txt
# ==========================定义函数Function==========================