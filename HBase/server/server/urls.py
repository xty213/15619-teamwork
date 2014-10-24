from django.conf.urls import patterns, include, url
from django.contrib import admin

urlpatterns = patterns('',
    # Examples:
    # url(r'^$', 'server.views.home', name='home'),
    # url(r'^blog/', include('blog.urls')),

    #url(r'^admin/', include(admin.site.urls)),
    url(r'^q0$', 'q0.views.handle_request'),
    url(r'^q1$', 'q1.views.handle_request'),
    url(r'^q2$', 'q2.views.handle_request'),
)
