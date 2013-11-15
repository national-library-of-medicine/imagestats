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
<title>ImageStats Login</title>
<body bgcolor="white">
<table><tbody valign="bottom"><tr>
<td id="header" align="left">
  <a href="https://pl.nlm.nih.gov/">
    <img id="leftHeaderLogoImg" src="https://pl.nlm.nih.gov/theme/lpf3/img/pl.png" alt="People Locator Logo">
  </a>
</td>
<td id="headerText" style="text-align: left">
			<span class="{style.homeBanner}">ImageStats</span><br/>
			<span>U.S. National Library of Medicine</span><br/>
			<span>Lister Hill National Center for Biomedical Communications</span>
</td>
<td align="right" valign="top">
  <a href="http://www.nlm.nih.gov/">
    <img src="https://pl.nlm.nih.gov/theme/lpf3/img/NLMlogoSmall.gif" alt="United States National Library of Medicine Logo">
  </a>
</td>
</tr>
</tbody></table>
<hr/>
<form method="POST" action='<%= response.encodeURL("j_security_check") %>' >
  <table border="0" cellspacing="5">
    <tr>
      <th align="right">Username:</th>
      <td align="left"><input type="text" name="j_username"></td>
    </tr>
    <tr>
      <th align="right">Password:</th>
      <td align="left"><input type="password" name="j_password"></td>
    </tr>
    <tr>
      <td align="right"><input type="submit" value="Log In"></td>
      <td align="left"><input type="reset"></td>
    </tr>
  </table>
</form>
</body>
</html>
