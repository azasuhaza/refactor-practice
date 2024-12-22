package com.aza.suhaza.refactor.breakfunction;

import com.aza.suhaza.refactor.util.PageCrawlerImpl;
import com.aza.suhaza.refactor.util.PageData;
import com.aza.suhaza.refactor.util.PathParser;
import com.aza.suhaza.refactor.util.SuiteResponder;
import com.aza.suhaza.refactor.util.WikiPage;
import com.aza.suhaza.refactor.util.WikiPagePath;

public class HtmlUtilClean {
  
  public static String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
    boolean isTestPage=pageData.hasAttribute("Test");
    boolean isSuite = true;
    
    if(isTestPage) {
      WikiPage testPage = pageData.getWikiPage();
      StringBuffer newPageContent = new StringBuffer();
      includeSetupPages(testPage, newPageContent, isSuite);
      newPageContent.append(pageData.getContent());
      includeTeardownPages(testPage, newPageContent, isSuite);
      pageData.setContent(newPageContent.toString());
    }

    return pageData.getHtml();
  }

  private static void includeSetupPages(WikiPage testPage,
    StringBuffer newPageContent, boolean isSuite) {
    WikiPage setUp = null;

    if(isSuite) {
      setUp = PageCrawlerImpl.getInheritedPage(
          SuiteResponder.SUITE_SETUP_NAME, testPage);
    } else {
      setUp = PageCrawlerImpl.getInheritedPage("SetUp", testPage);
    }
    
    WikiPagePath teardownPath = testPage.getPageCrawler().getFullPath(setUp);
    String teardownPathName = PathParser.render(teardownPath);
    newPageContent = appendPageInfo("setUp", teardownPathName, newPageContent);       
  }
  
  private static void includeTeardownPages(WikiPage testPage,
    StringBuffer newPageContent, boolean isSuite) {
    WikiPage tearDown = null;

    if(isSuite) {
      tearDown = PageCrawlerImpl.getInheritedPage(
          SuiteResponder.SUITE_TEARDOWN_NAME, testPage);
    } else {
      tearDown = PageCrawlerImpl.getInheritedPage("TearDown", testPage);
    }
    
    WikiPagePath teardownPath = testPage.getPageCrawler().getFullPath(tearDown);
    String teardownPathName = PathParser.render(teardownPath);
    newPageContent = appendPageInfo("TearDown", teardownPathName, newPageContent);
  }

  private static StringBuffer appendPageInfo(String pageType, String pathName, StringBuffer newPageContent) {
    if(pageType == "TearDown") {
      newPageContent.append("!include -taerdown .");      
    } else {
      newPageContent.append("!include -setup .");
    }
    newPageContent.append(pathName)
    .append("\n");
    return newPageContent;
  }
}
