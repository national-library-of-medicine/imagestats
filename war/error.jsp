<%--
 Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>
<html>
<head>
<title>Error Page For Examples</title>
</head>
<body bgcolor="white">
<table><tbody valign="bottom"><tr>
<td id="header" align="left">
  <a href="http://www.nlm.nih.gov/">
    <img id="LeftHeaderLogoImg" src="https://pl.nlm.nih.gov/theme/lpf3/img/NLMlogoSmall.gif" alt="United States National Library of Medicine Logo">
  </a>
</td>
<td id="headerText" style="text-align: left">
			<span class="homeBanner">ImageStats</span><br/>
			<span>U.S. National Library of Medicine</span><br/>
			<span>Lister Hill National Center for Biomedical Communications</span>
</td>
<td align="right" valign="top">
  <a href="https://sahanafoundation.org/">
    <img id="RightHeaderLogoImg" src="http://sahanafoundation.org/wp-content/uploads/2011/05/sahanalogo.jpg" alt="Sahana Logo">
  </a>
</td>
</tr>
</tbody></table>
Invalid username and/or password, please try
<a href='<%= response.encodeURL("/imagestats/ImageStats.html") %>'>again</a>.
</body>
</html>
