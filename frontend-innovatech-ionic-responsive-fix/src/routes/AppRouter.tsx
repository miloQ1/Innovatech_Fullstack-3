import type { ReactElement } from 'react';
import { IonRouterOutlet, IonSplitPane } from '@ionic/react';
import { IonReactRouter } from '@ionic/react-router';
import { Redirect, Route } from 'react-router-dom';
import { AuthLayout } from '../layouts/AuthLayout';
import { AppLayout } from '../layouts/AppLayout';
import { ProtectedRoute } from '../components/shared/ProtectedRoute';
import { Sidebar } from '../components/shared/Sidebar';
import { LoginPage } from '../pages/LoginPage';
import { RegisterPage } from '../pages/RegisterPage';
import { DashboardPage } from '../pages/DashboardPage';
import { ClientsPage } from '../pages/ClientsPage';
import { CreateClientPage } from '../pages/CreateClientPage';
import { ClientDetailPage } from '../pages/ClientDetailPage';
import { CreateProjectPage } from '../pages/CreateProjectPage';
import { ProjectDetailPage } from '../pages/ProjectDetailPage';
import { MyProjectsPage } from '../pages/MyProjectsPage';
import { ResourcesPage } from '../pages/ResourcesPage';

function protectedPage(page: ReactElement) {
  return (
    <ProtectedRoute>
      <AppLayout>{page}</AppLayout>
    </ProtectedRoute>
  );
}

function authPage(page: ReactElement) {
  return <AuthLayout>{page}</AuthLayout>;
}

export function AppRouter() {
  return (
    <IonReactRouter>
      <IonSplitPane contentId="main-content" when={false}>
        <Sidebar />
        <IonRouterOutlet id="main-content">
          <Route exact path="/login" render={() => authPage(<LoginPage />)} />
          <Route exact path="/register" render={() => authPage(<RegisterPage />)} />

          <Route exact path="/dashboard" render={() => protectedPage(<DashboardPage />)} />
          <Route exact path="/clients" render={() => protectedPage(<ClientsPage />)} />
          <Route exact path="/clients/create" render={() => protectedPage(<CreateClientPage />)} />
          <Route exact path={"/clients/:id(\\d+)"} render={() => protectedPage(<ClientDetailPage />)} />
          <Route exact path={"/clients/:clientId(\\d+)/projects/create"} render={() => protectedPage(<CreateProjectPage />)} />
          <Route exact path="/projects" render={() => protectedPage(<MyProjectsPage />)} />
          <Route exact path={"/projects/:id(\\d+)"} render={() => protectedPage(<ProjectDetailPage />)} />
          <Route exact path="/resources" render={() => protectedPage(<ResourcesPage />)} />

          <Route exact path="/" render={() => <Redirect to="/login" />} />
          <Route render={() => <Redirect to="/login" />} />
        </IonRouterOutlet>
      </IonSplitPane>
    </IonReactRouter>
  );
}
