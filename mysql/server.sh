
sudo mkfs.ext4 /dev/xvdb
sudo mkdir /var/lib/mysql
sudo mount /dev/xvdb /var/lib/mysql
sudo apt-get update
sudo apt-get install mysql-server-5.6

mysql -uroot -ppassword
GRANT ALL PRIVILEGES ON *.* TO 'ccteam'@'%' identified by 'password';

sudo vim /etc/mysql/my.cnf
#bind

sudo service mysql restart

source setup.sql