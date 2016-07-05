git pull

rm -rf target
rm -f devenv

if [ -z "$JAVA_HOME" ]; then
  JAVA_HOME=/opt/ict/java
fi

export PATH=/opt/ict/mvn/bin:$JAVA_HOME/bin:$PATH
mvn -Dmaven.test.skip=true clean package install assembly:assembly -U

ln -s target/ict-dtube-3.1.7/ict-dtube devenv
