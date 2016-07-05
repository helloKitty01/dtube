%{!?name:    %define name dtube}
%{!?version: %define version 1.0.6}
%{!?release: %define release 0}

%define install_dir /usr/local/dtube
%define init_dir /etc/init.d
%define link_dir /usr/local/bin
#%define doc_dir /usr/local/share/man

%define _builddir %(pwd)
%define _unpackaged_files_terminate_build 0

%define svn_code_rev %(svn info | grep 'Last Changed Rev' | awk '{ print $4; }')

summary:   dtube 
name:      %{name} 
version:   %{version}
release:   %{svn_code_rev}
vendor:    liupeifeng@ict.ac.cn
packager:  Manager
license:   Apache
group:     Applications/ICT
url:       http://www.ict.ac.cn
Buildroot: %{_tmppath}/%{name}-%{version}
Provides: dtube 

%description
DTube - An high-performance message processing midware designed and released by www.ict.ac.cn.

%prep

%build
exec `cat install.bat`

%install
[ "%{buildroot}" != "/" ] && rm -rf %{buildroot}
#make install
mkdir -p %{buildroot}/%{install_dir}/bin
cp -raf bin %{buildroot}/%{install_dir}/
cp -raf conf %{buildroot}/%{install_dir}/
cp -raf target/ict-dtube-1.0.6/ict-dtube/lib %{buildroot}/%{install_dir}/
cp -raf benchmark %{buildroot}/%{install_dir}/
cp -raf test %{buildroot}/%{install_dir}/
find %{buildroot}/%{install_dir}/ -name ".svn" | xargs rm -rf

%clean
[ "%{buildroot}" != "/" ] && rm -rf %{buildroot}

%post
ln -sf %{install_dir}/bin/checkmsg.sh     %{link_dir}
ln -sf %{install_dir}/bin/mqadmin    %{link_dir}
ln -sf %{install_dir}/bin/mqbroker    %{link_dir}
ln -sf %{install_dir}/bin/mqfiltersrv   %{link_dir}
ln -sf %{install_dir}/bin/mqnamesrv  %{link_dir}
ln -sf %{install_dir}/bin/mqshutdown %{link_dir}
ln -sf %{install_dir}/bin/os.sh %{link_dir}
ln -sf %{install_dir}/bin/play.sh %{link_dir}
ln -sf %{install_dir}/bin/runbroker.sh %{link_dir}
ln -sf %{install_dir}/bin/runserver.sh %{link_dir}
ln -sf %{install_dir}/bin/startfsrv.sh %{link_dir}
ln -sf %{install_dir}/bin/tools.sh %{link_dir}

%preun
rm -f %{link_dir}/checkmsg.sh     
rm -f %{link_dir}/mqadmin    
rm -f %{link_dir}/mqbroker    
rm -f %{link_dir}/mqfiltersrv   
rm -f %{link_dir}/mqnamesrv  
rm -f %{link_dir}/mqshutdown 
rm -f %{link_dir}/os.sh 
rm -f %{link_dir}/play.sh 
rm -f %{link_dir}/runbroker.sh 
rm -f %{link_dir}/runserver.sh 
rm -f %{link_dir}/startfsrv.sh 
rm -f %{link_dir}/tools.sh 

%postun

%files 
%defattr(0644,root,root)
%{install_dir}/conf

%defattr(0555,root,root)
%{install_dir}/lib

%defattr(0755,root,root)
%{install_dir}/bin
%{install_dir}/benchmark
%{install_dir}/test

%changelog

