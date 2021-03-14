from django.urls import path
from . import views
from rest_framework.urlpatterns import format_suffix_patterns



urlpatterns = [
    path('', views.index, name='home'),
    # path('test/', views.test.as_view()),
    path('zisad/api/covid_19/bd/COVID_model', views.COVID_model.as_view()),
]

urlpatterns = format_suffix_patterns(urlpatterns)